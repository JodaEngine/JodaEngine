$().ready(function(){

    $("#welcomeMessage").append("Welcome " + $.Storage.get("participantName") + ", here is your worklist:");

    // AJAX request to get the worklist for the selected participant
    $.ajax({
        type: 'GET',
        url: '/api/worklist/items',
        data: 'id='+$.Storage.get("participantUUID"),
        success: function(data) {
            var worklist = data;
            $.each(worklist, function(i, worklistitem){

                // TODO determine whether we have to claim a task or to start one
                $('#worklist').append("<tr id=" + worklistitem.id + " class=\"worklistitem\"> <td>" + worklistitem.task.subject + "</td><td> " + worklistitem.task.description + "</td><td><button class=\"claim\">Claim</button></td></tr>");
            });

            // Now add the click handlers to the freshly created buttons
            // Click handlers shall claim an item
            $("button.claim").click(function() {

                var worklistItemId = $(this).parents(".worklistitem").attr("id");

                $.ajax({
                    type: 'POST',
                    url: '/api/worklist/items/' + worklistItemId + '/claim',
                    data: $.Storage.get("participantUUID"), // maybe go back to queryparam (id= bla) for the participant UUID
                    success: function(data) {
                        console.log(data);
                        // be happy and do stuff (like morph button to start task or stuff like that)

                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        $('#participants').html(jqXHR.responseText).addClass('error');
                    },
                    contentType: 'text/html', // we send json
    });
            });

        },
        error: function(jqXHR, textStatus, errorThrown) {
            $('#worklist').html(jqXHR.responseText).addClass('error');
        },
        contentType: 'application/json', // we send json
        dataType: "json" // we expect json
    });
})

