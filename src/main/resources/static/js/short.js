

var lines = [];
var num_pending_request = 10;
var num_processed_lines = 0;
var retval = "";

var stompClient = null;

$(document).ready(function () {
    $("#username-header").html(getCookie("username"));
    getData();
    connect();
    $(".btn-get-started").click(function () {
        stompClient.send("/app/link", {}, JSON.stringify({url: $("#id-url-input").val(),
            idToken: getCookie("token")}));
    });

    $(function(){
        $("#upload_link").click(function(){
            $("#upload:hidden").trigger('click');

        });
    });

    $("#upload").change(function(e) {
        var ext = $("#upload").val().split(".").pop().toLowerCase();
        if($.inArray(ext, ["csv"]) == -1) {
            return false;
        }
        if (e.target.files != undefined) {
            var reader = new FileReader();
            reader.onload = function(e) {
                lines = e.target.result.split('\n');
                getShortURLFromCSV();
            };
            reader.readAsText(e.target.files.item(0));
        }
        return false;
    });
});


function connect() {
    alert("INTENTO CONECTAR");

    var socket = new SockJS('http://localhost:8080/short_url');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.info('Connected: ' + frame);
        stompClient.subscribe('/url_shortener/short_url', function (response) {
            appendRow(JSON.parse(response.body));
        });
    });
}

function appendRow(msg){
    alert(msg.error);
    var markup = "<tr><td class=\"first-column\"><a href=http://" + msg.target+ ">" + msg.target +"</td>" +
        "<td><a href=" + msg.uri + ">" +msg.uri + "</td><td class=\"last-column\">" +msg.clicks + "</td></tr>";
    var tableBody = $("tbody");
    tableBody.append(markup);
    $("#feedback").empty();
}


function getShortURLFromCSV() {
    $.ajax({
        type: "POST",
        url: URL_SERVER + "/link",
        data: {url: lines[num_processed_lines]},
        success: function (msg) {
            num_processed_lines ++;
            console.log(num_processed_lines + "  " + msg.uri);
            retval += msg.target + ";" + msg.uri + ";0\n";
            msg.clicks = 0;
            appendRow(msg);
            if (num_processed_lines < lines.length && lines[num_processed_lines] !== "") {
                getShortURLFromCSV();
            }else{
                download("result.csv", retval);
            }


        },
        error: function (jqXHR, textStatus, errorThrown) {
            if (num_processed_lines < lines.length) {
                retval += lines[num_processed_lines] + ";web no recortable;;\n";
                num_processed_lines++;
                getShortURLFromCSV();
            } else {
                download("result.csv", retval);
            }
        }
    });
}

function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}

function getData(){
    $.ajax({
        type: "POST",
        url: URL_SERVER + "/userlinks",
        success: function (msg) {
            links = msg.urlList;
            for(var i = 0; i < links.length; i ++){
                appendRow(links[i]);
                console.log(links[i].uri);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            window.location.replace(URL_SERVER + "/index.html")
        }
    });
}