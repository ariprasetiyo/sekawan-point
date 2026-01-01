import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1000,
  duration: '1m',
};

export default function () {
  const res = http.get('http://localhost:8080/test/vertx/rxJava3/repository/observable');

  check(res, {
    'status is 200': (r) => r.status === 200,
    'http error': (r) => r.status !== 200,
  });
}