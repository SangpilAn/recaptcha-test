let reCaptchaYN = false;

function init(){
    try{
        grecaptcha.ready(function() {
            grecaptcha.execute('6Lca15MpAAAAABeHW1UrPyc_ubGRrbIUgRz3U7JY', {action: 'login'}).then(function(token) {
                $("#captchaToken").val(token);
                reCaptchaYN = true;
            });
        });
    }catch (e){
        getCaptcha();
    }
}

function captchaCheck(){
    let url = "http://localhost:8080/captcha/check"
    if (!reCaptchaYN){
        url = "http://localhost:8080/captcha/simpleCaptcha/check"
    }
    let formData = $("#captchaForm").serialize();

    $.ajax({
        url:url,
        type:"POST",
        data:formData,
        beforeSend: function (){
            if (reCaptchaYN){
                grecaptcha.execute('6Lca15MpAAAAABeHW1UrPyc_ubGRrbIUgRz3U7JY', {action: 'login'}).then(function(token) {
                    if ($("#captchaToken").length){
                        $("#captchaToken").val(token);
                    }
                });
            }
        },
        success:function (data){
            console.log(data);
            if (data.status === 'OK'){
                $("#captchaForm")[0].method = "POST"
                $("#captchaForm")[0].action = data.action;
                $("#captchaForm").submit();
            }else{
                alert(data.message);
                getCaptcha();
            }
        },
        error:function (xhr, status, error){
            console.error("Error Status : "+status);
        }
    });

}

function getCaptcha(){
    let rand = Math.random();
    $('#captcha').html('<a href="javascript:getCaptcha();"><img src="http://localhost:8080/captcha/simpleCaptcha?captW=120&captH=35&captF=35&rand='+rand+'"/></a>');
    $("#captcha-div").css('display','');
}

window.onload = function () {
    init();
}