import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '1m',
};

export default function() {

    const cookie = 'vertx-web.session=vv1dYKcKW2qdaHEHi2fZDrOJLfsYw3Mpch7pCkCK90fuVyrNhOYh8dtw3guT3RA5ifKUt08BazIuIbeLeRQMw-FaT53ciYKDC2DM1JGlDR_h507frh3SdFlWuFChAlRZOpr69uFuckyqCtg1q8tAR6-sEPPbOpe0wdDiVyXBOordqhvHMi--enro3EIqLJeJU21SdNxDnromLf0CsPWxWbOrDd4pIYtEiQUny5pS0nw4xY2o9uWF4fHyPMygE051z9wB4IiWmSQIy8bwKWGv-85l3BhwCvEfxZaXzJWjQ0eXmvn2DClQWDLLcGRTkVCUBWoKteqUiYWPKvilO_xhNmKC8rlxK9AyW-cXdklLJqeMfOyMzTx36_k_MB2XtJVO71sjBJK63XweS_fBuABx7UxenclTINa4KRSxbl0LPHaj_PkrPoWa1kKGgAlGNRFEeAoWcsiMIwJqSfLT4B1xxToFfaASj8ivbRy-0u7u7F74yzI4KRgXNZOEvNG7rFT8cSTlRFWu1TosZGZlMmkZz_YxcclqminriKnPk8EWhw60DznedkjatnMnXayXuHuE0lliUFKtQLKNBFfYQreFq-hFP8xjPj2I8WO_3If7pqLA2mMKi3EvexaZH_7zesIWd1sUMcMvpqY15XePuLurUKT8HTa5SQlD4B8kVXXsNwXURM6N3JTFY3y3cSEKO4-jLU2Wp9Vqc057Cxr0KCXfiA8iUbBzp697CLgLWVUe5IPCZBoAkYeA6W1FcdPggVonzDZ2tQOqqcwFeNURjWyr_7sNCQtR'
    const headerParams = {
        headers: {
            'Content-Type': 'application/json',
            'x-request-id': 'hallo-uuid',
            'user-agent': 'Google chrome',
            'Cookie': cookie
        }
    };

    //user list
    const url = 'http://192.168.1.12:8080/api/v1/registration/user/list';
    const requestJson = JSON.stringify({
        requestId: "hallo-uuid",
        requestTime: "",
        type: "users",
        body: {}
    });

    //role list
    const urlRoles = 'http://192.168.1.12:8080/api/v1/registration/role/list';

    //    const res = http.post(url, requestJson, headerParams);
    const responses = http.batch([
        [
            'POST',
            url,
            requestJson,
            headerParams
        ],
        [
            'GET',
            urlRoles,
            null,
            headerParams
        ]
    ]);

    check(responses[0], {
                'user list status = 200': (r) => r.status === 200,
//                'user list status != 200': (r) => r.status !== 200,
            });

        check(responses[1], {
            'role list status = 200': (r) => r.status === 200,
//            'role list status != 200': (r) => r.status !== 200,
        });
}