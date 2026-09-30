import http from 'k6/http';
import { check } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL;
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN;
const VUS = Number(__ENV.VUS || 1);

const audioFile = open('./fixtures/sample.mp3', 'b');

const presignedApiDuration = new Trend(
    'presigned_backend_api_duration',
    true
);

const presignedFailed = new Rate('presigned_failed');

export const options = {
    scenarios: {
        presigned_burst: {
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
    const payload = JSON.stringify({
        fileName: 'sample.mp3',
        contentType: 'audio/mp3',
        fileSize: audioFile.byteLength,
    });

    const response = http.post(
        `${BASE_URL}/api/audio/uploads/presigned-url`,
        payload,
        {
            headers: {
                Authorization: `Bearer ${ACCESS_TOKEN}`,
                'Content-Type': 'application/json',
            },
            tags: {
                endpoint: 'presigned_url',
            },
        }
    );

    presignedApiDuration.add(response.timings.duration);

    const success = check(response, {
        'presigned status is 201': (r) => r.status === 201,
        'upload URL exists': (r) => {
            try {
                return !!r.json('result.uploadUrl');
            } catch (_) {
                return false;
            }
        },
    });

    presignedFailed.add(!success);
}