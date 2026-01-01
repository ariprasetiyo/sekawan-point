import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  scenarios: {
    high_load: {
      executor: 'constant-arrival-rate',
      rate: 100000,          // 100k requests per second
      timeUnit: '1s',
      duration: '60s',
      preAllocatedVUs: 2000, // must be enough to sustain the load
      maxVUs: 5000,
    },
  },
};

export default function () {
  http.get('http://localhost:8080/test/vertx/rxJava3/organic');
}