$(document).ready(function() {
    // Quando o formulário de cadastro é enviado
    $('#cadForm').submit(function(event) {
        event.preventDefault(); // Impede o envio tradicional do formulário

        // Captura os dados do formulário
        const cadData = {
            name: $('#username_cad').val(), // Nome de usuário
            password: $('#password_cad').val() // Senha
        };

        // Envia os dados via AJAX
        $.ajax({
            url: '/us/cad', // Endpoint do servidor
            type: 'POST', // Método HTTP
            contentType: 'application/json', // Tipo de conteúdo enviado
            data: JSON.stringify(cadData), // Converte os dados para JSON
            success: function(response) {
                // Exibe a resposta do servidor
                $('#nameReponse').text(response.userName); // Atualiza o nome de usuário na página
                $('#response').show(); // Exibe a div de resposta
                alert('Usuário criado com sucesso!'); // Feedback para o usuário
            },
            error: function(xhr) {
                // Exibe uma mensagem de erro em caso de falha
                alert('Erro ao criar usuário: ' + xhr.responseText);
            }
        });
    });
});