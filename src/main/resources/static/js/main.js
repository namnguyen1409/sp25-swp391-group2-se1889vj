uploadImage = async (file, type, maxSize, extensions, uploadUrl) => {
    if (!file.type.match(type)) {
        showModal('Modal', Context.DANGER, 'Lỗi', 'File không đúng định dạng');
        return;
    }
    if (file.size > maxSize * 1024 * 1024) {
        showModal('Modal', Context.DANGER, 'Cảnh báo', 'Kích thước file phải nhỏ hơn 2MB.');
        return;
    }
    const fileExtension = file.name.split('.').pop();
    if (!extensions.includes(fileExtension)) {
        showModal('Modal', Context.DANGER, 'Lỗi', 'File không đúng định dạng');
        return;
    }
    const formData = new FormData();
    formData.append('file', file);
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    try {
        const response = await fetch(uploadUrl, {
            method: 'POST',
            headers: {[csrfHeader]: csrfToken},
            body: formData,
        });
        const data = await response.json();
        if (data.status === 'success') {
            return data.url;
        } else {
            showModal('Modal', Context.DANGER, 'Upload ảnh thất bại', data.message);
            return null;
        }
    } catch (error) {
        console.log(error);
        showModal('Modal', Context.DANGER, 'Upload ảnh thất bại', 'Có lỗi xảy ra khi upload ảnh');
        return null;
    }
}