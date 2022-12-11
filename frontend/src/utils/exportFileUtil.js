import { jsPDF } from 'jspdf';
import 'jspdf-autotable';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import { Document, Packer, Paragraph, ShadingType, Table, TableCell, TableRow, TextRun, WidthType } from 'docx';

const TABLE_HEAD = ['Category', 'Total', 'Assigned', 'Available', 'Not available', 'Waiting for recycling', 'Recycled'];

const exportFile = {
  pdf: (data) => {
    const marginLeft = 40;
    const doc = new jsPDF('landscape', 'pt', 'A4');
    const title = 'Asset Report';
    const headers = [TABLE_HEAD];
    const bodyReport = data.map((item) => [
      item.name,
      item.count,
      item.assigned,
      item.available,
      item.notAvailable,
      item.waitingForRecycling,
      item.recycling,
    ]);
    const content = {
      startY: 50,
      head: headers,
      body: bodyReport,
    };
    doc.setFontSize(15);
    doc.text(title, marginLeft, 40);
    doc.autoTable(content);
    doc.save('Report.pdf');
  },
  xlsxCsv: (data, key) => {
    let ws = XLSX.utils.json_to_sheet(data);
    XLSX.utils.sheet_add_aoa(ws, [TABLE_HEAD]);
    let wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Asset Report');
    if (key === 'xlsx') {
      XLSX.writeFile(wb, 'Report.xlsx');
    }
    if (key === 'csv') {
      XLSX.writeFile(wb, 'Report.csv');
    }
  },
  docx: (data) => {
    const title = new Paragraph({
      children: [
        new TextRun({
          text: `Asset Report`,
          size: 32,
          bold: true,
        }),
        new TextRun({
          text: ` `,
          size: 32,
          bold: true,
          break: true,
        }),
      ],
    });
    const tableHeadRowData = new TableRow({
      children: TABLE_HEAD.map(
        (item) =>
          new TableCell({
            width: {
              size: 4505,
              type: WidthType.DXA,
            },
            shading: {
              type: ShadingType.CLEAR,
              fill: '2980BA',
            },
            children: [
              new Paragraph({
                children: [
                  new TextRun({
                    text: `${item}`,
                    size: 24,
                    bold: true,
                    color: 'FFFFFF',
                  }),
                ],
              }),
            ],
          }),
      ),
    });
    const tableBodyDataRow = data.map((dataValue) => {
      const listRowData = Object.values(dataValue).map(
        (objectValue) =>
          new TableCell({
            width: {
              size: 4505,
              type: WidthType.DXA,
            },
            children: [
              new Paragraph({
                children: [
                  new TextRun({
                    text: `${objectValue}`,
                    size: 22,
                  }),
                ],
              }),
            ],
          }),
      );
      return new TableRow({
        children: listRowData,
      });
    });
    const table = new Table({
      columnWidths: [4505, 4505],
      rows: [tableHeadRowData, ...tableBodyDataRow],
    });
    const doc = new Document({
      sections: [
        {
          children: [title, table],
        },
      ],
    });
    Packer.toBlob(doc).then((blob) => {
      saveAs(blob, 'Report.docx');
    });
  },
  html: (data) => {
    let htmlFile = `<!DOCTYPE html> <html> <head> <style> table {font-family: arial, sans-serif;border-collapse: collapse;width: 100%;}  td,
       th {   border: 1px solid #dddddd;   text-align: left;padding: 8px;} tr:nth-child(even) { background-color: #dddddd;} </style></head>
      <body> <h2>Asset Report</h2> 
      <table> <tr> <th>Category</th> <th>Total</th> <th>Assigned</th>  <th>Available</th>  <th>Not available</th>  <th>Waiting for recycling</th>  <th>Recycled</th></tr>`;
    for (let item in data) {
      htmlFile += `<tr><td>${data[item].name}</td> <td>${data[item].count}</td> <td>${data[item].assigned}</td> <td>${data[item].available}</td>
       <td>${data[item].notAvailable}</td> <td>${data[item].waitingForRecycling}</td> <td>${data[item].recycling}</td></tr>`;
    }
    htmlFile += '</table></body></html>';
    const blob = new Blob([htmlFile], { type: 'text/html' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.download = 'Report.html';
    link.href = url;
    link.click();
  },
};

export default exportFile;
