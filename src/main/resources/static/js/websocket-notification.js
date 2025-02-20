document.addEventListener("DOMContentLoaded", function () {
    // Kết nối WebSocket
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('🔗 Kết nối WebSocket thành công: ' + frame);

        // Lắng nghe thông báo từ server
        stompClient.subscribe('/topic/notifications', function (message) {
            showNotification(message.body);
        });
    });

    // Hiển thị thông báo trên trang
    function showNotification(message) {
        console.log('🔔 ' + message);
        const notification = document.createElement('div');
        notification.classList.add('notification');
        notification.innerText = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 5000);
    }


    const style = document.createElement('style');
    style.innerHTML = `
        .notification {
            position: fixed;
            top: 20px;
            right: 20px;
            background-color: #4CAF50;
            color: white;
            padding: 15px;
            border-radius: 5px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            z-index: 1000;
            font-size: 16px;
            animation: fadein 0.5s, fadeout 0.5s 4.5s;
        }
        @keyframes fadein {
            from { opacity: 0; right: 0; }
            to { opacity: 1; right: 20px; }
        }
        @keyframes fadeout {
            from { opacity: 1; right: 20px; }
            to { opacity: 0; right: 0; }
        }
    `;
    document.head.appendChild(style);
});
