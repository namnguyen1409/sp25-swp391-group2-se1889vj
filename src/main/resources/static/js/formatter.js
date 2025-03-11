formatPhone = (phone) => {
    return phone.replace(/(\d{4})(\d{3})(\d{3})/, '$1 $2 $3');
}

$('.phone').each(function() {
    $(this).text(formatPhone($(this).text()));
});

formatDate = (date) => {
    let d = new Date(date);
    return d.toLocaleDateString('vi-VN');
}

$('.date').each(function() {
    $(this).text(formatDate($(this).text()));
});

formatDateTime = (dateTime) => {
    let d = new Date(dateTime);
    return d.toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

$('.dateTime').each(function() {
    $(this).text(formatDateTime($(this).text()));
});

formatCurrency = (currency) => {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(currency);
}

currencyToRaw = (currency) => {
    return currency.replace(/\D/g, '');
}

$('.currency').each(function() {
    $(this).text(formatCurrency($(this).text()));
});

$('.price').each(function() {
    $(this).text(formatCurrency($(this).text()));
});

$('.currencyInput').each(function() {
    $(this).val(formatCurrency($(this).val()));
});


function numberToVietnamese(n) {
    if (n === 0) return "không đồng";

    const units = ["", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"];
    const tens = ["", "mười", "hai mươi", "ba mươi", "bốn mươi", "năm mươi", "sáu mươi", "bảy mươi", "tám mươi", "chín mươi"];
    const places = ["", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ", "tỷ tỷ"];

    function convertThreeDigits(num) {
        let str = "";
        let hundred = Math.floor(num / 100);
        let ten = Math.floor((num % 100) / 10);
        let unit = num % 10;

        if (hundred > 0) {
            str += units[hundred] + " trăm ";
        }
        if (ten > 1) {
            str += tens[ten] + " ";
            if (unit > 0) str += units[unit];
        } else if (ten === 1) {
            str += "mười ";
            if (unit === 5) str += "lăm";
            else if (unit > 0) str += units[unit];
        } else if (unit > 0) {
            if (hundred > 0) str += "lẻ ";
            if (unit === 5) str += "năm";
            else str += units[unit];
        }
        return str.trim();
    }

    let isNegative = n < 0;
    n = Math.abs(n);

    let parts = [];
    let i = 0;
    while (n > 0) {
        let chunk = n % 1000;
        if (chunk > 0) {
            let chunkStr = convertThreeDigits(chunk);
            if (places[i]) chunkStr += " " + places[i];
            parts.unshift(chunkStr);
        }
        n = Math.floor(n / 1000);
        i++;
    }

    let result = parts.join(" ").trim() + " đồng";
    return isNegative ? "âm " + result : result;
}
