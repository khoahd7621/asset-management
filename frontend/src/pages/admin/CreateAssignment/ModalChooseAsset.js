import { useEffect, useState } from 'react';
import { Button, Input, Modal, Table } from 'antd';
import { CaretDownOutlined } from '@ant-design/icons';

import './Modal.scss';

import { getAllAssetsByStatus } from '../../../services/getApiService';

const ModalChooseAsset = ({ open, onCancel, currentAsset, handleSaveChoose }) => {
  const { Search } = Input;
  const TableColumns = [
    {
      title: (
        <span>
          Asset code <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'assetCode',
      key: 'assetCode',
      ellipsis: true,
      sorter: (a, b) => a.assetCode.localeCompare(b.assetCode),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '120px',
    },
    {
      title: (
        <span>
          Asset Name <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'assetName',
      key: 'assetName',
      ellipsis: true,
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: (
        <span>
          Category <CaretDownOutlined />
        </span>
      ),
      dataIndex: 'category',
      key: 'category',
      ellipsis: true,
      sorter: (a, b) => a.category.localeCompare(b.category),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '180px',
    },
  ];

  const [datas, setDatas] = useState([]);
  const [selectedKey, setSelectedKey] = useState([currentAsset.assetCode]);
  const [currentData, setCurrentData] = useState({
    ...currentAsset,
  });
  const [dataSource, setDataSource] = useState([...datas]);

  useEffect(() => {
    fetchListAssets();
  }, []);

  const fetchListAssets = async () => {
    const response = await getAllAssetsByStatus('AVAILABLE');
    if (response && response.status === 200) {
      setDatas(
        response?.data.map((item, index) => {
          return {
            key: item.id,
            assetCode: item.assetCode,
            assetName: item.assetName,
            category: item?.category?.name,
          };
        }),
      );
      setDataSource(
        response?.data.map((item, index) => {
          return {
            key: item.id,
            assetCode: item.assetCode,
            assetName: item.assetName,
            category: item?.category?.name,
          };
        }),
      );
    }
  };

  const handleOnSearch = (keyword) => {
    setDataSource(
      datas.filter(
        (asset) =>
          asset?.assetCode?.toLowerCase()?.includes(keyword) || asset?.assetName?.toLowerCase()?.includes(keyword),
      ),
    );
  };

  const handleSave = () => {
    handleSaveChoose('ASSET', currentData);
  };

  return (
    <Modal className="modal-choose" open={open} closable={false} footer={false} width={'700px'} mask={false}>
      <div className="modal-choose__header">
        <div className="title">Select Asset</div>
        <Search
          id="choose-asset-search-input"
          allowClear
          className="search-input"
          onSearch={(keyword) => handleOnSearch(keyword)}
          enterButton
        />
      </div>
      <div className="modal-choose__table">
        <Table
          id="choose-asset-table"
          size="small"
          rowSelection={{
            type: 'radio',
            selectedRowKeys: selectedKey,
            onChange: (selectedRowKeys, selectedRows) => {
              setCurrentData({
                assetId: selectedRowKeys[0],
                assetName: selectedRows[0].assetName,
              });
              setSelectedKey(selectedRowKeys);
            },
          }}
          showSorterTooltip={false}
          columns={TableColumns}
          dataSource={dataSource}
          pagination={false}
          onRow={(record, rowIndex) => ({
            onClick: () => {
              if (record.key !== selectedKey[0]) {
                setCurrentData({
                  assetId: record.key,
                  assetName: record.assetName,
                });
                setSelectedKey([record.key]);
              }
            },
          })}
        />
      </div>
      <div className="modal-choose__action">
        <Button className="save" type="primary" htmlType="submit" disabled={!currentData.assetId} onClick={handleSave}>
          Save
        </Button>
        <Button htmlType="button" onClick={() => onCancel()}>
          Cancel
        </Button>
      </div>
    </Modal>
  );
};

export default ModalChooseAsset;
