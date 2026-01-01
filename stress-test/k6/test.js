import http from 'k6/http';

export const options = {
  vus: 1000,
  duration: '1s',
};

export default function () {
  http.get('http://localhost:8080/test/vertx/rxJava3/organic');
}