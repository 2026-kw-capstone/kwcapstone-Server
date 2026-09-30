import http from 'k6/http';
import { check } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL;
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN;
const VUS = Number(__ENV.VUS || 1);

const audioFile = open('./fixtures/sample.mp3', 'b');

const presignDuration = new Trend('e2e_presign_duration', true);
const s3PutDuration = new Trend('e2e_s3_put_duration', true);
const completeDuration = new Trend('e2e_complete_duration', true);
const totalDuration = new Trend('presigned_e2e_duration', true);

const e2eFailed = new Rate('presigned_e2e_failed');

export const options = {
    scenarios: {
        presigned_e2e_burst: {
            executor: 'per-vu-iterations',
            vus: VUS,
            iterations: 1,
            maxDuration: '2m',
        },
    },

    summaryTrendStats: [
        'avg',
        'min',
        'med',
        'max',
        'p(90)',
        'p(95)',
        'p(99)',
        'count',
    ],
};

export default function () {
    const e2eStart = Date.now();

    // 1. Presigned PUT URL 발급
    const presignResponse = http.post(
        `${BASE_URL}/api/audio/uploads/presigned-url`,
        JSON.stringify({
            fileName: 'sample.mp3',
            contentType: 'audio/mp3',
            fileSize: audioFile.byteLength,
        }),
        {
            headers: {
                Authorization: `Bearer ${ACCESS_TOKEN}`,
                'Content-Type': 'application/json',
            },
            tags: {
                endpoint: 'presign',
            },
        }
    );

    presignDuration.add(presignResponse.timings.duration);

    const presignSuccess = check(presignResponse, {
        'presign status is 201': (r) => r.status === 201,
    });

    if (!presignSuccess) {
        e2eFailed.add(true);
        return;
    }

    let result;

    try {
        result = presignResponse.json().result;
    } catch (_) {
        e2eFailed.add(true);
        return;
    }

    if (!result?.uploadUrl || !result?.key) {
        e2eFailed.add(true);
        return;
    }

    // 2. Client -> S3 직접 PUT
    const s3Response = http.put(
        result.uploadUrl,
        audioFile,
        {
            headers: {
                // Presign 시 서명한 Content-Type과 반드시 동일
                'Content-Type': result.contentType,
            },
            tags: {
                endpoint: 's3_put',
            },
        }
    );

    s3PutDuration.add(s3Response.timings.duration);

    const s3Success = check(s3Response, {
        'S3 PUT status is 200': (r) => r.status === 200,
    });

    if (!s3Success) {
        e2eFailed.add(true);
        return;
    }

    // 3. 업로드 완료 검증
    const completeResponse = http.post(
        `${BASE_URL}/api/audio/uploads/complete`,
        JSON.stringify({
            key: result.key,
        }),
        {
            headers: {
                Authorization: `Bearer ${ACCESS_TOKEN}`,
                'Content-Type': 'application/json',
            },
            tags: {
                endpoint: 'complete',
            },
        }
    );

    completeDuration.add(completeResponse.timings.duration);

    const completeSuccess = check(completeResponse, {
        'complete status is 200': (r) => r.status === 200,
    });

    if (!completeSuccess) {
        e2eFailed.add(true);
        return;
    }

    // Presign 시작 ~ S3 업로드 + Complete 검증 완료
    totalDuration.add(Date.now() - e2eStart);

    e2eFailed.add(false);
}