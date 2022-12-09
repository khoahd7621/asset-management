const exportFile = {
  pdf: (data) => {
    const { jsPDF } = require('jspdf');
    require('jspdf-autotable');

    const marginLeft = 40;
    const doc = new jsPDF('landscape', 'pt', 'A4');

    doc.setFontSize(15);

    const title = 'Assignment Report';
    const headers = [
      ['Category', 'Total', 'Assigned', 'Available', 'Not available', 'Waiting for recycling', 'Recycled'],
    ];
    const bodyReport = data.map((item) => [
      item.name,
      item.count,
      item.assigned,
      item.available,
      item.notAvailable,
      item.waitingForRecycling,
      item.recycling,
    ]);

    let content = {
      startY: 50,
      head: headers,
      body: bodyReport,
    };

    doc.text(title, marginLeft, 40);
    doc.autoTable(content);
    doc.save('report.pdf');
  },

  xlsxCsv: (data, key) => {
    let XLSX = require('xlsx');
    let ws = XLSX.utils.json_to_sheet(data);
    XLSX.utils.sheet_add_aoa(ws, [
      ['Category', 'Total', 'Assigned', 'Available', 'Not available', 'Waiting for recycling', 'Recycled'],
    ]);
    let wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Report');
    if (key === 'xlsx') {
      XLSX.writeFile(wb, 'Report.xlsx');
    }
    if (key === 'csv') {
      XLSX.writeFile(wb, 'Report.csv');
    }
  },
};

export default exportFile;
