var site = {
    init: function () {
        site.getSite();
    },

    getSite: function () {
      $('#site').change(function () {
          $('#selected-site').html($(this).find("option:selected").text();
      });
    }

}
$(document).ready( function () {
    $('#joblistTable').DataTable();
} );

$(site.init);