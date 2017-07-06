/**
 * Created by Karol Janowski on 2017-07-01.
 */
function checkBillingAddress() {
    if ($("#theSameAsShippingAddress").is(":checked")){
        $(".billingAddress").prop("disabled", true);
    } else {
        $(".billingAddress").prop("disabled", false);

    }
}

function checkPasswordMatch() {
    var password = $("#txtNewPassword").val();
    var confirmPassword = $("#txtConfirmPassword").val();
    if (password==0 && confirmPassword==0){
        $("#checkPasswordMatch").html("");
        $("#updateUserInfoButton").prop('disabled', false);
    } else {
        if (password!=confirmPassword){
        $("#checkPasswordMatch").html("Passwords mismatch!");
        $("#updateUserInfoButton").prop('disabled', true);
        } else {
            $("#checkPasswordMatch").html("Passwords match");
            $("#updateUserInfoButton").prop('disabled', false);
        }
    }
}

$(document).ready(function () {
    $(".cartItemQty").on('change', function () {
        var id=this.id;
        $('#update-item-'+id).css('display', 'inline-block');
    });
    $("#theSameAsShippingAddress").on('click', checkBillingAddress);
    $("#txtNewPassword").keyup(checkPasswordMatch);
    $("#txtConfirmPassword").keyup(checkPasswordMatch);
});