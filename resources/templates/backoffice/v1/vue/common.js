export const hostServer = "http://192.168.1.4:8080";

export function getClientInfo() {
    const uuid = generateUUIDv4();
    const userAgent = navigator.userAgent;
    return {
        uuid,
        userAgent
    };
}

export function isValidPhoneNumber(value) {
    const regex = /^(?:\+62|62|0)8[1-9][0-9]{6,10}$/;
    return regex.test(value);
}

export function isValidAlphabetic(value) {
    const regex = /^[A-Za-zÀ-ÖØ-öø-ÿ\s]+$/;
    return regex.test(value);
}

export function isValidEmail(value) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(value);
}

export function isValidDate(value){

    if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;
    const d = new Date(value);
    return !isNaN(d) && d.toISOString().slice(0, 10) === value;
}

export function formatDate(date) {
    const pad = (n) => n.toString().padStart(2, '0');

    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1);
    const day = pad(date.getDate());
    const hour = pad(date.getHours());
    const minute = pad(date.getMinutes());
    const second = pad(date.getSeconds());

    return '${year}-${month}-${day} ${hour}:${minute}:${second}';
}

export function generateUUIDv4() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, c => {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

export function unauthorizedRedirect(response) {
    // 🚨 Handle unauthorized / forbidden
    if (response != null && response.status != null && (response.status === 401 || response.status === 403)) {
        window.location.href = "/forbidden"; // or router push
        return;
    }

    // ❌ Other server errors
    if (response != null && !response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
}

export function fetchPOST(url, clientInfo, requestBodyJson) {
    return fetch(
        hostServer + url, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'x-request-id': clientInfo.uuid,
            },
            body: requestBodyJson
        }
    );
}

export function fetchGET(url, clientInfo) {

    return fetch(
        hostServer + url, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'x-request-id': clientInfo.uuid
                //                            'x-user-agent':  info.userAgent ,
                //                            'Cookie': document.cookie
            }
        }
    );
}

export async function fetchPOSTFull(url, clientInfo, requestJson){
var response = null;
try {
                response =  await fetchPOST(url, clientInfo, requestJson);
                unauthorizedRedirect(response);
                // Deserialize here
                return await response.json();
            } catch (err) {
                unauthorizedRedirect(response);
                console.error("Error system:", err);
            }
}

export async function fetchGETFull(url, clientInfo){
var response = null;
try {
                response =  await fetchGET(url, clientInfo);
                unauthorizedRedirect(response);
                // Deserialize here
                return await response.json();
            } catch (err) {
                unauthorizedRedirect(response);
                console.error("Error system:", err);
            }
}

export function number_format(number, decimals, dec_point, thousands_sep) {
    // *     example: number_format(1234.56, 2, ',', ' ');
    // *     return: '1 234,56'
    number = (number + '').replace(',', '').replace(' ', '');
    var n = !isFinite(+number) ? 0 : +number,
        prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
        sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
        dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
        s = '',
        toFixedFix = function(n, prec) {
            var k = Math.pow(10, prec);
            return '' + Math.round(n * k) / k;
        };
    // Fix for IE parseFloat(0.55).toFixed(0) = 0;
    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
    if (s[0].length > 3) {
        s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
    }
    if ((s[1] || '').length < prec) {
        s[1] = s[1] || '';
        s[1] += new Array(prec - s[1].length + 1).join('0');
    }
    return s.join(dec);
}