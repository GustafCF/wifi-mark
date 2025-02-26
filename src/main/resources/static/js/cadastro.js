$(document).ready(function() {
    $('#cadForm').submit(function(event) {
        event.preventDefault();

        const username = $('#username_cad').val();
        const password = $('#password_cad').val();

        const cadData = {
            username: username,
            password: password
        };

        $.ajax({
            url: '/us/cad',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(cadData),
            success: function(response) {
                $('#nameReponse').text(response.username);
                $('#response').show();
                alert('Usuário criado com sucesso!');
                window.location.href = '/';
            },
            error: function(xhr) {
                $('#error-text').text(xhr.responseText || 'Erro ao criar usuário.');
                $('#error-message').show();
            }
        });
    });
});