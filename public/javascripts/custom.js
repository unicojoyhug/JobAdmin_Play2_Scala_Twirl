var admin = {
    init: function () {
        admin.getSite();
        admin.setDataTable();
        admin.setDeleteJobAlert();
        admin.setCreateAlert();
        admin.setEditAlert();
        admin.setDatePicker();
        admin.validator();
    },

    getSite: function () {
        var name = "finanswatch.dk";
        var id = 1
        var status = "unexpiredJobs";
        $('#jobIframe').attr("src", "jobs/" + name + "/" + status);
        $('#newjob').attr("href", "/job/newJob/" + name + "/" + id);
        $('#addJobBtn').attr("href", "/job/newJob/" + name + "/" + id);

        $('#site').change(function () {
            name = $(this).find("option:selected").text().trim();
            id = $(this).find("option:selected").val();

            $('#jobIframe').attr("src", "jobs/" + name + "/unexpiredJobs");
            $('#newjob').attr("href", "/job/newJob/" + name + "/" + id);
            $('#addJobBtn').attr("href", "/job/newJob/" + name + "/" + id);
            $('.btn.btn-default.btn-on.btn-md').addClass('active');
            $('.btn.btn-default.btn-off.btn-md').removeClass('active');

        });

        $('#status').change(function () {
            if(status.match('unexpiredJobs')){
                status = "expiredJobs";
            }else if(status.match('expiredJobs')){
                status = "unexpiredJobs";
            }

            name = $('#site').find("option:selected").text().trim();
            id = $('#site').find("option:selected").val();

            $('#jobIframe').attr("src", "jobs/" + name + "/" + status);
            $('#newjob').attr("href", "/job/newJob/" + name + "/" + id);
            $('#addJobBtn').attr("href", "/job/newJob/" + name + "/" + id);

        });

    },

    setDataTable: function () {
        $.fn.dataTable.moment( 'DD-MM-YYYY' );

        $('#joblistTable').dataTable({
            "language": {
                "lengthMenu": "Vis _MENU_ jobs per side",
                "zeroRecords": "Intet Job findes",
                "info": " Siden _PAGE_ af _PAGES_",
                "infoEmpty": "No records available",
                "infoFiltered": "(valgt fra _MAX_ total jobs)",
                "search": "Søg",
                "paginate": {
                    "first": "Først side",
                    "next": "Næste",
                    "previous": "Før",
                    "last": "Sidst"
                }
            },
            "lengthMenu": [[5, 10, 25, 50, -1], [5, 10, 25, 50, "All"]]
        });
        $('#companyListTable').dataTable({
            "language": {
                "lengthMenu": "Vis _MENU_ virksomheder per side",
                "zeroRecords": "Intet virksomhed findes",
                "info": " Siden _PAGE_ af _PAGES_",
                "infoEmpty": "No records available",
                "infoFiltered": "(valgt fra _MAX_ total virksomheder)",
                "search": "Søg",
                "paginate": {
                    "first": "Først side",
                    "next": "Næste",
                    "previous": "Før",
                    "last": "Sidst"
                }
            },
            "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]]
        });

    },

    setDeleteJobAlert: function () {
        $('.btn-delete-submit').on('click', function (e) {

            e.preventDefault();
            var form = $(this).parents('form');

            swal({
                title: "Er du sikker?",
                text: "Jobbet vil blive slettet.",
                icon: "warning",
                buttons: true,
                buttons: ["Annuller", "Slet"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    swal("Jobbet er slettet.", {
                        icon: "success",
                    });

                    form.submit();
                }else {
                    swal("Annulleret", "Jobbet bliver ikke slettet.", "error");
                }
            });
        });
    },

    setCreateAlert: function () {
        $('.btn-create-submit').on('click', function (e) {

            e.preventDefault();
            var form = $(this).parents('form');

            var alertType = $(this).attr('data-alert-type');

            swal({
                title: "Er du sikker?",
                text: alertType + " vil blive oprettet.",
                icon: "warning",
                buttons: true,
                buttons: ["Annuller", "Opret"],
                dangerMode: false,
            }).then((willCreate) => {
                if (willCreate) {
                    swal(alertType +" er oprettet.", {
                        icon: "success",
                    });
                    form.submit();

                }else {
                    swal("Annulleret", alertType +" bliver ikke oprettet.", "error");
                }
            });
        });

    },

    setEditAlert: function () {
        $('.btn-edit-submit-alert').on('click', function (e) {

            e.preventDefault();
            var form = $(this).parents('form');

            var alertType = $(this).attr('data-alert-type');

            swal({
                title: "Er du sikker?",
                text: alertType + " vil blive ændret.",
                icon: "warning",
                buttons: true,
                buttons: ["Annuller", "Rediger"],
                dangerMode: false,
            }).then((willCreate) => {
                if (willCreate) {
                    swal(alertType +" er ændret.", {
                        icon: "success",
                    });
                    form.submit();

                }else {
                    swal("Annulleret", alertType +" bliver ikke ændret.", "error");
                }
            });
        });

    },

    setDatePicker: function(){

        $.fn.datepicker.defaults.format = "dd/mm/yyyy";
        $.fn.datepicker.defaults.language = "da";
        $.fn.datepicker.defaults.calendarWeeks = true;
        $.fn.datepicker.defaults.todayHighlight = true;

    },

    validator: function(){
        $("form").bootstrap.validator()

    }
}


$(admin.init);

$("#radio_externallink").click(
    function() {
        $("#externallink").prop("disabled", false);
        $("#pdf").prop("disabled", true);
        $("#pdf").val('');

    });
$("#raido_pdf").click(
    function() {
        $("#externallink").val('');
        $("#externallink").prop("disabled", true);
        $("#pdf").prop("disabled", false);
        $("#pdf").click();

    });
