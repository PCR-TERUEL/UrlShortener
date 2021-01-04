

var lines = [];
var num_pending_request = 10;
var num_processed_lines = 0;
var retval = "";

var stompClient = null;
var numUrlsCsvSends = 0;
var numUrlsCsvReceive = 0;

$(document).ready(function () {
    $("#username-header").html(getCookie("username"));
    getData();
    connect();
    $(".btn-get-started").click(function () {
        numUrlsCsvSends++;
        stompClient.send("/app/link", {}, JSON.stringify({url: $("#id-url-input").val(),
            idToken: getCookie("token"), numMonth: $("#id-expired-input").val()}));
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


/**
 * Do a petition to connect with the web socket server.
 * If the connection was successful subscribe to the queue for get the messages from the server and show the short url.
 */
function connect() {
    var socket = new SockJS(URL_SERVER + '/short_url');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.info('Connected: ' + frame);
        stompClient.subscribe('/user/url_shortener/short_url', function (response) {
            dealMessageFromServer(JSON.parse(response.body));
        });
        stompClient.subscribe('/user/url_shortener/validation_url', function (validation) {
            let msg = JSON.parse(validation.body);
            if(msg.valid) {
                document.getElementById(msg.shortUrl).href = msg.shortUrl;
            }
            if(msg.csv){
                addUrlCsvFile(msg);
            }
        });
    });
}

function addUrlCsvFile(msg) {
    if(msg.valid) {
        retval += msg.url + ";" + msg.shortUrl + ";" + "0\n";
    } else {
        retval += msg.url + ";web no alcanzable;\n";
    }
    numUrlsCsvReceive++;
    if(numUrlsCsvSends <= numUrlsCsvReceive && numUrlsCsvSends !== 0){
        download('results.csv', retval);
        numUrlsCsvReceive = 0;
        numUrlsCsvSends = 0;
    }
}

/**
 * Deal with the message receive from the server
 * @param msg message send by the server
 */
function dealMessageFromServer(msg) {
    if(!msg.error) {
        appendRow(msg);
    }
}

/**
 * Add a short url receive by message in the table of the GUI.
 * @param msg: message with the data of the short url
 */
function appendRow(msg){
        var markup
        markup = "<tr><td class=\"first-column\"><a href=" + msg.target+ ">" + msg.target +"</td>" +
            "<td><a id=" + msg.uri + ">" +msg.uri + "</td><td class=\"last-column\">" +msg.clicks + "</td></tr>";
        var tableBody = $("tbody");
        tableBody.append(markup);
        $("#feedback").empty();
}

/**
 * Send the requests to short the urls from the csv file upload by the user
 */
function getShortURLFromCSV() {
    num_processed_lines++;
    stompClient.send("/app/link", {}, JSON.stringify({url: lines[num_processed_lines - 1],
        idToken: getCookie("token"), documentCsv: true}));
    if (num_processed_lines < lines.length && lines[num_processed_lines] !== "") {
        getShortURLFromCSV();
    } else {
        numUrlsCsvSends = num_processed_lines;
        num_processed_lines = 0;
    }
}

/**
 * Create and download a file to the user
 * @param filename name of the file to create
 * @param text content of the file to crate
 */
function download(filename, text) {
    alert("DESCARGANDO " + text);
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}

/**
 * Obtain and show the data of the user
 */
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