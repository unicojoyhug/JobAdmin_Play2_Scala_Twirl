var site = {
    init: function () {
        site.getSite();
        site.setDataTable();
    },

    getSite: function () {
        var name = "finanswatch.dk";
        $('#jobIframe').attr("src", "jobs/"+name);

        $('#site').change(function () {
             name = $(this).find("option:selected").text().trim();
            $('#jobIframe').attr("src", "jobs/"+name);
      });

    },

    setDataTable: function() {
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
                    "last":"Sidst"
                }
            }
        });
    }

}


$(site.init);