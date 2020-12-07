
$(document).ready(function () {
    container = $("#container");

    $("#signUp").click(function () {
        container.addClass("right-panel-active");


    });
    $("#signIn").click(function () {
        container.removeClass("right-panel-active");
    });

    $("#login-button").click(function () {
        if(validateLoginFields($("#login-username"), $("#login-password"))) {
            container.removeClass("right-panel-active");
            $.ajax({
                type: "POST",
                url: URL_SERVER + "/login",
                data: {username: $("#login-username").val(), password: $("#login-password").val()},
                success: function (msg, statusText, xhr) {
                    if (xhr.status != 200) {
                        var feedbackDiv = $("#login-feedback");
                        feedbackDiv.empty();
                        feedbackDiv.html("El usuario o la contraseña erróneos");
                    } else {
                        location.href = "panel"
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.status)
                    if (jqXHR.status === 400) {
                        var feedbackDiv = $("#login-feedback");
                        feedbackDiv.empty();
                        feedbackDiv.html("Error");
                    }
                }
            });
        }
    });

    $("#register").click(function (){
        if(validateRegisterFields($("#register-username"), $("#register-password"), $("#register-confirm-password"))){
            $.ajax({
                type: "POST",
                url: URL_SERVER + "/register",
                data: {username: $("#register-username").val(), password: $("#register-password").val()},
                success: function (msg, statusText, xhr) {
                    if (xhr.status === 201) {
                        document.cookie = "uuid=" + msg.uuid;
                        document.cookie = "username=" + $("#register-username").val();
                        window.location.replace(URL_SERVER + "/panel.html")
                    }else if (xhr.status === 226){
                        var feedbackDiv = $("#register-feedback");
                        feedbackDiv.empty();
                        feedbackDiv.html("El usuario ya existe");
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.status)
                    if (jqXHR.status === 400) {
                        var feedbackDiv = $("#register-feedback");
                        feedbackDiv.empty();
                        feedbackDiv.html("Error");
                    }
                }
            });
        }
    });
});

function validateLoginFields(login, password){
    var ok = true;
    if(login.val() === ""){

        login.addClass("invalid");
        login.attr("placeholder", "Usuario vacío");
        ok = false;
    } else {
        login.removeClass("invalid");
    }

    if(password.val() === ""){
        password.addClass("invalid");
        password.attr("placeholder", "Contraseña vacía");
        ok = false;
    } else {
        password.removeClass("invalid");
    }

    return ok;
}

function validateRegisterFields(login, password, confirmPassword){
    let ok = true;
    if(login.val() === ""){
        login.addClass("invalid");
        login.attr("placeholder", "Usuario vacío");
        ok = false;
    } else {
        login.removeClass("invalid");
    }

    if(password.val() === ""){
        password.addClass("invalid");
        password.attr("placeholder", "Contraseña vacía");
        ok = false;
    } else {
        password.removeClass("invalid");
    }

    if(confirmPassword.val() === ""){
        confirmPassword.addClass("invalid");
        confirmPassword.attr("placeholder", "Contraseña vacía");
        ok = false;
    } else {
        password.removeClass("invalid");
    }
    if(confirmPassword.val() != password.val()){
        password.addClass("invalid");
        confirmPassword.addClass("invalid");
        $("#register-feedback").html("Las contraseñas no coinciden");
        ok = false;
    }else{
        password.removeClass("invalid");
        confirmPassword.removeClass("invalid");
        $("#register-feedback").html("");
    }
    return ok;
}