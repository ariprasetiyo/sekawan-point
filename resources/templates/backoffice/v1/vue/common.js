export const hostServer = "http://192.168.1.4:8080";

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

export function unauthorizedRedirect(responseRoleList) {
    // 🚨 Handle unauthorized / forbidden
    if (responseRoleList != null && responseRoleList.status != null && (responseRoleList.status === 401 || responseRoleList.status === 403)) {
        window.location.href = "/forbidden"; // or router push
        return;
    }

    // ❌ Other server errors
    if (responseRoleList != null && !responseRoleList.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
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