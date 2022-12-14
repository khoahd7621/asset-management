import { useEffect, useState } from 'react';
import { Button, Input, Modal, Table } from 'antd';
import { CaretDownOutlined, CaretUpOutlined } from '@ant-design/icons';

import { searchAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination } from '../../services/findApiService';
import CustomPagination from '../Pagination/Pagination';

const ModalChooseAsset = ({ open, onCancel, currentAsset, handleSaveChoose }) => {
  const { Search } = Input;
  const [field, setField] = useState();
  const [order, setOrder] = useState();

  function onChangeSortOrder(_pagination, _filters, sorter, _extra) {
    setField(sorter.field);
    setOrder(sorter.order);
  }

  const title = (title, dataIndex) => {
    return (
      <span>
        {title} {order === 'ascend' && field === dataIndex ? <CaretUpOutlined /> : <CaretDownOutlined />}
      </span>
    );
  };
  const TableColumns = [
    {
      title: title('Asset Code', 'assetCode'),
      dataIndex: 'assetCode',
      key: 'assetCode',
      ellipsis: true,
      sorter: (a, b) => a.assetCode.localeCompare(b.assetCode),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '120px',
    },
    {
      title: title('Asset Name', 'assetName'),
      dataIndex: 'assetName',
      key: 'assetName',
      ellipsis: true,
      sorter: (a, b) => a.assetName.localeCompare(b.assetName),
      sortDirections: ['ascend', 'descend', 'ascend'],
    },
    {
      title: title('Category', 'category'),
      dataIndex: 'category',
      key: 'category',
      ellipsis: true,
      sorter: (a, b) => a.category.localeCompare(b.category),
      sortDirections: ['ascend', 'descend', 'ascend'],
      width: '180px',
    },
  ];

  const PAGE_SIZE = 10;
  const ASSET_STATUS = 'AVAILABLE';

  const [totalRow, setTotalRow] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchKeywords, setSearchKeywords] = useState('');
  const [datas, setDatas] = useState([]);
  const [selectedKey, setSelectedKey] = useState([currentAsset.assetCode]);
  const [currentData, setCurrentData] = useState({
    ...currentAsset,
  });
  const [dataSource, setDataSource] = useState([...datas]);

  useEffect(() => {
    fetchListAssets(searchKeywords, ASSET_STATUS, '', PAGE_SIZE, currentPage - 1, '', '');
  }, []);

  const fetchListAssets = async (keyWord, statuses, categories, limit, page, sortField, sortType) => {
    const response = await searchAssetsWithKeywordAndStatusesAndCategoryIdsWithPagination({
      keyWord,
      statuses,
      categories,
      limit,
      page,
      sortField,
      sortType,
    });
    if (response && response.status === 200) {
      setDatas(
        response?.data?.data.map((item, _index) => {
          return {
            key: item.id,
            assetCode: item.assetCode,
            assetName: item.assetName,
            category: item?.category?.name,
          };
        }),
      );
      setDataSource(
        response?.data?.data.map((item, _index) => {
          return {
            key: item.id,
            assetCode: item.assetCode,
            assetName: item.assetName,
            category: item?.category?.name,
          };
        }),
      );
      setTotalRow(response?.data?.totalRow);
    }
  };

  const handleChangePage = (current) => {
    setCurrentPage(current);
    fetchListAssets(searchKeywords, ASSET_STATUS, '', PAGE_SIZE, current - 1, '', '');
  };

  const handleOnSearch = (keyword) => {
    fetchListAssets(keyword, ASSET_STATUS, '', PAGE_SIZE, 0, '', '');
    setCurrentPage(1);
    setSearchKeywords(keyword);
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
          onChange={onChangeSortOrder}
          onRow={(record, _rowIndex) => ({
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
      <div className="modal-choose__pagination">
        <CustomPagination
          onChange={handleChangePage}
          current={currentPage}
          defaultPageSize={PAGE_SIZE}
          total={totalRow}
        />
      </div>
      <div className="modal-choose__action">
        <Button className="save" type="primary" htmlType="submit" disabled={!currentData.assetId} onClick={handleSave}>
          Save
        </Button>
        <Button
          htmlType="button"
          onClick={() => {
            setSelectedKey([currentAsset.assetCode]);
            setCurrentData({ ...currentAsset });
            onCancel();
          }}
        >
          Cancel
        </Button>
      </div>
    </Modal>
  );
};

export default ModalChooseAsset;
