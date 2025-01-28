const Context = {
    INFO: {
        icon: 'bi bi-info-circle-fill',
        clazz: 'alert alert-primary'
    },
    SUCCESS: {
        icon: 'bi bi-check-circle-fill',
        clazz: 'alert alert-success'
    },
    WARNING: {
        icon: 'bi bi-exclamation-triangle-fill',
        clazz: 'alert alert-warning'
    },
    DANGER: {
        icon: 'bi bi-x-circle-fill',
        clazz: 'alert alert-danger'
    }
}


let showModal = function (modalId, context, title, body) {
    let modal = $('#' + modalId);
    modal.find('.modal-dialog').addClass(context.clazz);
    modal.find('.modal-title').html('<i class="' + context.icon + '"></i> ' + title);
    modal.find('.modal-body').text(body);
    let btModal = new bootstrap.Modal(modal);
    btModal.show();
}

