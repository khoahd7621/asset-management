import { Button, Table } from 'antd';
import React, { useEffect, useState } from 'react';

import './Report.scss';

import { getReportDetails } from '../../../services/getApiService';
import { SortIcon } from '../../../assets/CustomIcon';

export const Report = () => {
  const [reportDetails, setReportDetails] = useState([]);

  const onClickToExport = () => {
    let XLSX = require('xlsx');
    let ws = XLSX.utils.json_to_sheet(reportDetails);
    XLSX.utils.sheet_add_aoa(ws, [
      ['Category', 'Total', 'Assigned', 'Available', 'Waiting for recycling', 'Not available', 'Recycled'],
    ]);
    let wb = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Report');
    XLSX.writeFile(wb, 'Report.xlsx');
  };

  const title = (title) => {
    return (
      <div id="frame">
        <div>{title}</div>
        <div>
          <SortIcon />
        </div>
      </div>
    );
  };

  useEffect(() => {
    getData();
  }, []);

  const getData = async () => {
    const response = await getReportDetails();
    if (response.status === 200) {
      setReportDetails(response.data);
    }
  };

  const columns = [
    {
      width: '8em',
      title: title('Category'),
      dataIndex: 'name',
      key: 'category',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      width: '6em',
      title: title('Total'),
      dataIndex: 'count',
      ellipsis: true,
      key: 'total',
      defaultSortOder: 'ascend',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.count - b.count,
    },
    {
      width: '6em',
      title: title('Assigned'),
      dataIndex: 'assigned',
      ellipsis: true,
      key: 'assigned',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.assigned - b.assigned,
    },
    {
      width: '6em',
      title: title('Available'),
      dataIndex: 'available',
      key: 'available',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.available - b.available,
    },
    {
      width: '6em',
      title: title('Not available'),
      dataIndex: 'notAvailable',
      key: 'notavailable',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.notAvailable - b.notAvailable,
    },
    {
      width: '8em',
      title: title('Waiting for recycling'),
      dataIndex: 'waitingForRecycling',
      key: 'waitingforrecycling',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.waitingForRecycling - b.waitingForRecycling,
    },
    {
      width: '4em',
      title: title('Recycled'),
      dataIndex: 'recycling',
      key: 'recycled',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.recycling - b.recycling,
    },
    {
      width: '0em',
      title: '',
      dataIndex: 'none',
      key: 'none',
      ellipsis: true,
    },
  ].filter((item) => !item.hidden);

  return (
    <div className="report">
      <div className="report__title">Report</div>
      <div className="report__function">
        <Button className="report__button" onClick={onClickToExport}>
          Export
        </Button>
      </div>
      <br></br>
      <div className="report__body">
        <Table
          id="report__table"
          showSorterTooltip={false}
          size="small"
          sortDirections={'ascend'}
          pagination={false}
          className="user-list"
          dataSource={reportDetails}
          columns={columns}
        />
      </div>
    </div>
  );
};
