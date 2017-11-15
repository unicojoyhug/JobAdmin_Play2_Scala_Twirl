var site = {
    init: function () {
        site.getSite();
        site.setDataTable();
        site.setDeleteJobAlert();
        site.setCreateJobAlert();
        site.setDatePicker();
        site.validator();
    },

    getSite: function () {
        var name = "finanswatch.dk";
        var id = 1
        $('#jobIframe').attr("src", "jobs/" + name);
        $('#newjob').attr("href", "/job/newJob/" + name + "/" + id);
        $('#addJobBtn').attr("href", "/job/newJob/" + name + "/" + id);

        $('#site').change(function () {
            name = $(this).find("option:selected").text().trim();
            id = $(this).find("option:selected").val()
            $('#jobIframe').attr("src", "jobs/" + name);
            $('#newjob').attr("href", "/job/newJob/" + name + "/" + id);
            $('#addJobBtn').attr("href", "/job/newJob/" + name + "/" + id);

        });

    },

    setDataTable: function () {
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
                    swal("Jobbet er slettet", {
                        icon: "success",
                    });

                    form.submit();
                }else {
                    swal("Annulleret", "Jobbet bliver ikke slettet.", "error");
                }
            });
        });
    },
    setCreateJobAlert: function () {
        $('.btn-create-submit').on('click', function (e) {
            e.preventDefault();
            var form = $(this).parents('form');

            swal({
                title: "Er du sikker?",
                text: "Jobbet vil blive oprettet.",
                icon: "warning",
                buttons: true,
                buttons: ["Annuller", "Opret"],
                dangerMode: false,
            }).then((willCreate) => {
                if (willCreate) {
                    swal("Jobbet er oprettet", {
                        icon: "success",
                    });
                    form.submit();

                }else {
                    swal("Annulleret", "Jobbet bliver ikke oprettet.", "error");
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
        $("form").bootstrap.valida

    }
}


$(site.init);
