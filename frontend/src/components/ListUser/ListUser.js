import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { Table, Row, Col, Modal, Input, Button } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';

import { getItems } from '../../services/findApiService';
import { adminRoute } from '../../routes/routes';

import { CloseIcon, EditIcon, SortIcon } from '../../assets/CustomIcon';
import { CheckboxMenu } from './HandleFilter';
import Paging from './HandlePaging';

import './ListUser.scss';

const ListUser = () => {
  const user = useSelector((state) => state.user.user);
  const navigate = useNavigate();
  const { Search } = Input;
  const [userList, setUserList] = useState([]);
  const [current, setCurrent] = useState(1);
  const [type, setType] = useState(['ALL']);
  const [userDetail, setUserDetail] = useState({});
  const [checkValue, setCheckValue] = useState(['ALL']);

  useEffect(() => {
    getData();
  }, [current]);

  const getData = async () => {
    const response = await getItems(`/api/find?location=${user.location}&pageNumber=${current - 1}`);
    if (response.status === 200) {
      setUserList(response.data);
    }
  };

  const ApiUserDetails = async (staffCode) => {
    const response = await getItems(`/api/find/get/${staffCode}`);
    if (response.status === 200) {
      setUserDetail(response.data);
    }
  };

  const [isModalVisible, setIsModalVisible] = useState(false);

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

  const columns = [
    {
      width: '110px',
      title: title('Staff Code'),
      dataIndex: 'staffCode',
      key: 'staffcode',
      sortDirections: ['ascend'],
      sorter: (a, b) => a.staffCode.match(/\d+/)[0] - b.staffCode.match(/\d+/)[0],
    },
    {
      width: '120px',
      title: title('Full Name'),
      dataIndex: 'fullName',
      key: 'fullname',
      defaultSortOder: 'ascend',
      sortDirections: ['ascend'],
      sorter: (a, b) => a.fullName.length - b.fullName.length,
      render: (text, record) => (
        <a className="user-list" data-id={record.staffCode} onClick={showModal}>
          {text}
        </a>
      ),
    },
    {
      width: '110px',
      title: 'Username',
      dataIndex: 'username',
      key: 'username',
    },
    {
      width: '250px',
      title: title('Joined Date'),
      dataIndex: 'joinedDate',
      key: 'joineddate',
      sortDirections: ['ascend'],
      sorter: (a, b) => Date.parse(a.joinedDate) / 1000 - Date.parse(b.joinedDate) / 1000,
    },
    {
      width: '100px',
      title: title('Type'),
      dataIndex: 'type',
      key: 'type',
      sortDirections: ['ascend'],
      sorter: (a, b) => a.type.localeCompare(b.type),
    },
    {
      align: 'center',
      key: 'options',
      dataIndex: 'status',
      title: '',
      render: () => (
        <div id="frame">
          <div className="edit-icon">
            <EditIcon />
          </div>
          <div></div>
          <div></div>
          <div>
            <CloseCircleOutlined style={{ color: 'red' }} />
          </div>
        </div>
      ),
    },
  ].filter((item) => !item.hidden);

  const showModal = (staffcode) => {
    ApiUserDetails(staffcode.currentTarget.dataset.id);
    setIsModalVisible(true);
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };

  const onCheckboxChange = async (selection) => {
    if (selection === 'ADMIN' || selection === 'STAFF') {
      setCheckValue(selection);
      setType([selection]);
      const response = await getItems(`/api/find/filter/${current - 1}?type=${selection}&location=${user.location}`);
      if (response.status === 200) {
        setUserList(response.data);
      }
    } else {
      const response = await getItems(`/api/find/filter/${current - 1}?location=${user.location}`);
      if (response.status === 200) {
        setUserList(response.data);
      }
      setCheckValue(['ALL']);
      setType(['ADMIN', 'STAFF']);
    }
  };
  const onSearch = async (value) => {
    let url = '';
    if (type === 'ALL' || type.length === 2 || type.length === 0) {
      url = `/api/find/search?name=${value}&staffCode=${value}&location=${user.location}&page=${current - 1}`;
    } else {
      url = `/api/find/search?name=${value}&staffCode=${value}&type=${type}&location=${user.location}&page=${
        current - 1
      }`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      setUserList(response.data);
    }
  };

  return (
    <div className="list-user-wrapper">
      <div>
        <Row>
          <Col xs={20} sm={4} md={6} lg={10} xl={11}>
            <CheckboxMenu options={['ALL', 'ADMIN', 'STAFF']} value={checkValue} onChange={onCheckboxChange} />
          </Col>
          <Col xs={20} sm={16} md={12} lg={10} xl={8}>
            <Search
              maxLength={100}
              className="handle-search"
              allowClear
              onSearch={onSearch}
              style={{ width: 220 }}
            ></Search>
          </Col>
          <Col xs={2} sm={4} md={6} lg={4} xl={4}>
            <Button
              className="handle-button"
              onClick={() => navigate(`/${adminRoute.home}/${adminRoute.manageUser}/${adminRoute.createUser}`)}
            >
              Create new user
            </Button>
          </Col>
        </Row>
      </div>
      <br></br>
      <Table
        showSorterTooltip={false}
        size="small"
        sortDirections={'ascend'}
        pagination={false}
        className="user-list"
        dataSource={userList.data}
        columns={columns}
      />
      <br></br>
      <div className="user-list">
        <Paging total={userList['totalRow']} current={current} onChange={setCurrent} />
      </div>

      <Modal
        className="user-list"
        mask={false}
        title={'Detailed User Information'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        footer={null}
        closeIcon={<CloseIcon />}
      >
        <Row>
          <Col span={8}>
            <p>Staff Code</p>
            <p>Full Name</p>
            <p>Username</p>
            <p>Date of Birth</p>
            <p>Gender</p>
            <p>Joined Date</p>
            <p>Type</p>
            <p>Location</p>
          </Col>

          <Col span={16}>
            <p>{userDetail?.staffCode}</p>
            <p>{userDetail?.fullName}</p>
            <p>{userDetail?.username}</p>
            <p>{userDetail?.dateOfBirth}</p>
            <p>{userDetail?.gender}</p>
            <p>{userDetail?.joinedDate}</p>
            <p>{userDetail?.type}</p>
            <p>{userDetail?.location}</p>
          </Col>
        </Row>
      </Modal>
    </div>
  );
};

export default ListUser;
