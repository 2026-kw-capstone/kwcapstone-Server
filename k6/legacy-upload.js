import http from 'k6/http';
import { check } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL;
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN;
const VUS = Number(__ENV.VUS || 1);

const audioFile = open('./fixtures/sample.mp3', 'b');

const legacyApiDuration = new Trend(
    'legacy_backend_api_duration',
    true
);

const legacyFailed = new Rate('legacy_failed');

export const options = {
    scenarios: {
        legacy_burst: {
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
    const payload = {
        file: http.file(
            audioFile,
            'sample.mp3',
            'audio/mp3'
        ),
    };

    const response = http.post(
        `${BASE_URL}/api/audio/uploads/benchmark/legacy`,
        payload,
        {
            headers: {
                Authorization: `Bearer ${ACCESS_TOKEN}`,
            },
            tags: {
                endpoint: 'legacy_upload',
            },
        }
    );

    legacyApiDuration.add(response.timings.duration);

    const success = check(response, {
        'legacy upload status is 201': (r) => r.status === 201,
    });

    legacyFailed.add(!success);
}