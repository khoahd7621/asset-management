import { Button, Dropdown, Space, Spin, Table } from 'antd';
import React, { useEffect, useState } from 'react';
import { CaretUpOutlined, CaretDownOutlined } from '@ant-design/icons';

import './Report.scss';

import { getReportDetails } from '../../../services/getApiService';
import exportFile from '../../../utils/exportFileUtil';

const Report = () => {
  const [reportDetails, setReportDetails] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const onClickToExportXlsx = () => {
    exportFile.xlsxCsv(reportDetails, 'xlsx');
  };

  const onClickToExportPdf = () => {
    exportFile.pdf(reportDetails);
  };

  const onClickToExportCsv = () => {
    exportFile.xlsxCsv(reportDetails, 'csv');
  };

  const onClickToExportDocx = () => {
    exportFile.docx(reportDetails);
  };

  const onClickToExportHTML = () => {
    exportFile.html(reportDetails);
  };

  const items = [
    {
      key: '1',
      label: (
        <a target={'_blank'} rel="noopener noreferrer" onClick={onClickToExportXlsx}>
          XLSX
        </a>
      ),
    },
    {
      key: '2',
      label: (
        <a target={'_blank'} rel="noopener noreferrer" onClick={onClickToExportPdf}>
          PDF
        </a>
      ),
    },
    {
      key: '3',
      label: (
        <a target={'_blank'} rel="noopener noreferrer" onClick={onClickToExportCsv}>
          CSV
        </a>
      ),
    },
    {
      key: '4',
      label: (
        <a target={'_blank'} rel="noopener noreferrer" onClick={onClickToExportDocx}>
          DOCX
        </a>
      ),
    },
    {
      key: '5',
      label: (
        <a target={'_blank'} rel="noopener noreferrer" onClick={onClickToExportHTML}>
          HTML
        </a>
      ),
    },
  ];

  const [field, setField] = useState();
  const [order, setOrder] = useState();

  function onChangeSortOrder(_pagination, _filters, sorter, _extra) {
    setField(sorter.field);
    setOrder(sorter.order);
  }

  const title = (title, dataIndex) => {
    return (
      <div id="frame">
        <span>
          {title} {order === 'ascend' && field === dataIndex ? <CaretUpOutlined /> : <CaretDownOutlined />}
        </span>
      </div>
    );
  };

  useEffect(() => {
    document.title = 'Report';
    setIsLoading(true);
    getData();
  }, []);

  const getData = async () => {
    const response = await getReportDetails();
    if (response.status === 200) {
      setIsLoading(false);
      setReportDetails(response.data);
    }
  };

  const columns = [
    {
      width: '8em',
      title: title('Category', 'name'),
      dataIndex: 'name',
      key: 'category',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.name.localeCompare(b.name),
      render: (text, _record) => <div className="col-btn">{text}</div>,
    },
    {
      width: '6em',
      title: title('Total', 'count'),
      dataIndex: 'count',
      ellipsis: true,
      key: 'total',
      defaultSortOder: 'ascend',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.count - b.count,
      render: (text, _record) => <div className="col-btn">{text}</div>,
    },
    {
      width: '6em',
      title: title('Assigned', 'assigned'),
      dataIndex: 'assigned',
      ellipsis: true,
      key: 'assigned',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.assigned - b.assigned,
      render: (text, _record) => <div className="col-btn">{text}</div>,
    },
    {
      width: '6em',
      title: title('Available', 'available'),
      dataIndex: 'available',
      key: 'available',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.available - b.available,
      render: (text, _record) => <div className="col-btn">{text}</div>,
    },
    {
      width: '6em',
      title: title('Not available', 'notAvailable'),
      dataIndex: 'notAvailable',
      key: 'notavailable',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.notAvailable - b.notAvailable,
      render: (text, _record) => <div className="col-btn">{text}</div>,
    },
    {
      width: '8em',
      title: title('Waiting for recycling', 'waitingForRecycling'),
      dataIndex: 'waitingForRecycling',
      key: 'waitingforrecycling',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.waitingForRecycling - b.waitingForRecycling,
      render: (text, _record) => <div className="col-btn">{text}</div>,
    },
    {
      width: '4em',
      title: title('Recycled', 'recycling'),
      dataIndex: 'recycling',
      key: 'recycled',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.recycling - b.recycling,
      render: (text, _record) => <div className="col-btn">{text}</div>,
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
        <Dropdown
          overlayClassName="report__drop-down"
          menu={{
            items,
          }}
          placement="bottomLeft"
          trigger={'click'}
        >
          <Button className="report__button">Export</Button>
        </Dropdown>
      </div>
      <br></br>
      <div className="report__body">
        {isLoading ? (
          <Space size="middle">
            <Spin size="large" style={{ paddingLeft: '30rem', paddingTop: '10rem' }} />
          </Space>
        ) : (
          <Table
            onChange={onChangeSortOrder}
            id="report__table"
            showSorterTooltip={false}
            size="small"
            sortDirections={'ascend'}
            pagination={false}
            className="user-list"
            dataSource={reportDetails}
            columns={columns}
          />
        )}
      </div>
    </div>
  );
};

export default Report;
