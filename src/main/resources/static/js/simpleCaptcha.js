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
        reCaptchaYN = false;
    }
}

function captchaCheck(){
    let url = "/captcha/check"
    if (!reCaptchaYN){
        url = "/captcha/simpleCaptcha/check"
    }

    $("#captchaAnswer").val($("#userAnswer").val());
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
                $("#loginForm")[0].method = "POST"
                $("#loginForm")[0].action = data.action;
                $("#loginForm").submit();
            }else{
                alert(data.message + "\n보안 문자를 입력해주세요.");
                reCaptchaYN = false;
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
    $('#captcha').html('<a href="javascript:getCaptcha();"><img src="/captcha/simpleCaptcha?rand='+rand+'"/></a>');
    $("#captcha-div").css('display','');
}

window.onload = function () {
    init();
}