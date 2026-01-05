import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1000,
    duration: '1m',
};

export default function() {

    const cookie = 'vertx-web.session=B3cndqS6xxlKK7mImNtqMBMp_aw9vkjxM2uiwVLBAduNijDzRxaPLVVogE8K75tR_6sRr3kdp-fQl8L7jRUN32Q-RqxZlqnv2yJmpK5mKIqzE9pCqturCItkw1iFnuCd1dlmrlKjXr7Tqgd_W5NGnqsWCj8n3yq8hYOAtixOREMq608NceNsTorB1-gByk55V2-J_l3onry5UUk9MZEPjH8YJ2yP7_YeBOk5eqh1la7RmgtrhHfp3icmx5iGmgx7qkdYu6ih2pTFS9Aw9iqK9Vkv89KAzX-Y4MOZdHJX-plYv0qVC6cgeaUkPsf98Yr1J6AUkiZhEDht4FfvQBO6_tO-oyauuoKZtFck0gD6qe18EOOF7XiAH9deSV3Aqa_MI56HX8kZnSfijyqFH8EXdGTftg-nzhQzcSg4DedIT2Cn21cnuwBB-h70n4VxksmLJvq9a03NtamjOH_lnQE0gS3T4-c5XX55WdIsaQfLGY27YcZxR2s4BTiPpd34nM8I6F3QGLU0qolgvG8-RBECy3V3zeWabXCn8vjBmyWA6I4wqRnsJnD1ToCbit_W-tliID4SQ920hlSEMeVNR7SX_MbRXnoVF71-4QI86QapJq4As3cdfVEeYage9kz2Y07zWOWlAAbt7E8K2CGoNOycaPWhT7XwPHAX_rn6PZkdxYDYZHcor0MWiexjStUIB28Y3a94pFV_w3Os-iZUOV_HCVr2xkBXwKJkIg1sxZyepRpUoqMrbhMP5Pzdo-HCjUipOXtPX3h6JDTxOfm6OoSa3boDf5mZ6Wm_TPaaqVLmvpwkNQDPw5bhJjqnneStcqwTRgY6o-j526n9_O1JoV1VR-8'
    const headerParams = {
        headers: {
            'Content-Type': 'application/json',
            'x-request-id': 'hallo-uuid',
            'user-agent': 'Google chrome',
            'Cookie': cookie
        }
    };

    //user list
    const url = 'http://localhost:8080/api/v1/registration/user/list';
    const requestJson = JSON.stringify({
        requestId: "hallo-uuid",
        requestTime: "",
        type: "users",
        body: {}
    });

    //role list
    const urlRoles = 'http://localhost:8080/api/v1/registration/role/list';

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