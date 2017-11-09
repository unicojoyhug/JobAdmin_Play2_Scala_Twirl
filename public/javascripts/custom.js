var site = {
    init: function () {
        site.getSite();
        site.setDataTable();
        site.setDeleteAlert();
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
        $('#joblistTable').DataTable({
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
    },

    setDeleteAlert: function () {
        $('.btn-submit').on('click', function (e) {
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
                    form.submit();
                    swal("Jobbet er slettet", {
                        icon: "success",
                    });
                }else {
                    swal("Annulleret", "Jobbet bliver ikke slettet.", "error");
                }
            });
        });
    }
}


$(site.init);
