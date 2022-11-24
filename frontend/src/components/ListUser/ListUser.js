import { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { useLocation, useNavigate } from 'react-router-dom';
import { Table, Row, Col, Modal, Input, Button, Checkbox, Popover } from 'antd';
import { CloseCircleOutlined } from '@ant-design/icons';

import { getItems } from '../../services/findApiService';
import { adminRoute } from '../../routes/routes';

import { CloseIcon, EditIcon, SortIcon, FilterIcon } from '../../assets/CustomIcon';
import Paging from './HandlePaging';

import './ListUser.scss';

const ListUser = () => {
  const user = useSelector((state) => state.user.user);
  const location = useLocation();
  const navigate = useNavigate();
  const { Search } = Input;
  const [userList, setUserList] = useState([]);
  const [current, setCurrent] = useState(1);
  const [type, setType] = useState(['ALL']);
  const [userDetail, setUserDetail] = useState({});
  const [checkValue, setCheckValue] = useState(['ALL']);

  // Handle Filter
  const CheckboxGroup = Checkbox.Group;
  const plainOptions = ['ADMIN', 'STAFF'];
  const defaultCheckedList = ['ALL'];
  const [checkedList, setCheckedList] = useState([]);
  const [checkAll, setCheckAll] = useState(false);
  const [isFilter, setIsFilter] = useState(false);
  const [isSearch, setIsSearch] = useState(false);
  const [searchValue, setSearchValue] = useState('');

  const formatDate = (joineddate) => {
    var initial = joineddate.split(/\//);
    const newdate = new Date([initial[1], initial[0], initial[2]].join('/'));
    return newdate.getTime();
  };

  const onChange = async (list) => {
    let url = `/api/find/filter/0?location=${user.location}`;
    setType(list);
    setIsFilter(true);
    setIsSearch(false);
    setSearchValue('');
    setCurrent(1);
    if (list.some((data) => data === 'STAFF' || data === 'ADMIN') && list.length < 2) {
      url = `/api/find/filter/0?type=${list}&location=${user.location}`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      setUserList(response.data);
    }
    setCheckedList(list);
    setCheckAll(list.length === plainOptions.length);
    if (list.length === plainOptions.length) {
      setType(['ADMIN', 'STAFF']);

      setCheckAll(true);
      setCheckedList(defaultCheckedList);
      const response = await getItems(`/api/find/filter/0?location=${user.location}`);
      if (response.status === 200) {
        setUserList(response.data);
      }
    }
  };

  const onCheckAllChange = async (e) => {
    setCurrent(1);
    setCheckedList([]);
    setCheckValue(['ADMIN', 'STAFF']);
    setIsFilter(false);

    setType(['ADMIN', 'STAFF']);

    const response = await getItems(`/api/find/filter/0?location=${user.location}`);
    if (response.status === 200) {
      setUserList(response.data);
    }
    setCheckAll(e.target.checked);
  };

  const content = (
    <div style={{ display: 'list-item' }}>
      <Checkbox
        defaultChecked={defaultCheckedList}
        onChange={onCheckAllChange}
        checked={checkAll}
        className="checkbox-filter"
      >
        ALL
      </Checkbox>
      <CheckboxGroup options={plainOptions} value={checkedList} onChange={onChange} className="checkbox-filter" />
    </div>
  );

  useEffect(() => {
    getData();
  }, [current]);

  const getData = async () => {
    let url = `/api/find?location=${user.location}&pageNumber=${current - 1}`;
    if (isFilter === true && isSearch === false) {
      if (type.length < 2) {
        url = `/api/find/filter/${current - 1}?type=${type}&location=${user.location}`;
      }
      url = `/api/find/filter/${current - 1}?location=${user.location}`;
    }
    if (isFilter === true && isSearch === true) {
      url = `/api/find/search?name=${searchValue}&staffCode=${searchValue}&type=${type}&location=${
        user.location
      }&page=${current - 1}`;
    }
    if (isFilter === false && isSearch === true) {
      url = `/api/find/search?name=${searchValue}&staffCode=${searchValue}&location=${user.location}&page=${
        current - 1
      }`;
    }
    const response = await getItems(url);
    if (response.status === 200) {
      const newUserCreate = location.state?.userCreateResponse;
      if (newUserCreate && newUserCreate.username) {
        const listDatas = response?.data.data.filter((item) => item.username !== newUserCreate.username);
        listDatas.unshift(newUserCreate);
        setUserList({
          ...response.data,
          data: listDatas,
        });
        window.history.replaceState({}, document.title);
      } else {
        setUserList(response.data);
      }
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
      width: '8em',
      title: title('Staff Code'),
      dataIndex: 'staffCode',
      key: 'staffcode',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.staffCode.match(/\d+/)[0] - b.staffCode.match(/\d+/)[0],
    },
    {
      width: '10em',
      title: title('Full Name'),
      dataIndex: 'fullName',
      ellipsis: true,
      key: 'fullname',
      defaultSortOder: 'ascend',
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => a.fullName.localeCompare(b.fullName),
      render: (text, record) => (
        <a className="user-list" data-id={record.staffCode} onClick={showModal}>
          {text}
        </a>
      ),
    },
    {
      width: '10em',
      title: 'Username',
      dataIndex: 'username',
      ellipsis: true,
      key: 'username',
    },
    {
      width: '12em',
      title: title('Joined Date'),
      dataIndex: 'joinedDate',
      key: 'joineddate',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
      sorter: (a, b) => formatDate(a.joinedDate) - formatDate(b.joinedDate),
    },
    {
      width: '5em',
      title: title('Type'),
      dataIndex: 'type',
      key: 'type',
      ellipsis: true,
      sortDirections: ['ascend', 'desencd', 'ascend'],
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

  const onSearch = async (value) => {
    setCurrent(1);
    let url = '';

    setIsSearch(true);
    setSearchValue(value);
    if (value === '') {
      setIsSearch(false);
    }
    if (type.some((data) => data === 'ALL') || type.length === 2 || type.length === 0) {
      url = `/api/find/search?name=${value}&staffCode=${value}&location=${user.location}&page=0`;
    } else {
      url = `/api/find/search?name=${value}&staffCode=${value}&type=${type}&location=${user.location}&page=0`;
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
            <Popover
              content={content}
              placement="bottom"
              trigger="click"
              overlayClassName="list-user-dropdown-box-type"
            >
              <Button className="handle-filter">
                <Row>
                  <Col span={21}>Type</Col>
                  <Col span={1} className="border-right"></Col>
                  <Col span={2}>
                    <FilterIcon type="filter" />
                  </Col>
                </Row>
              </Button>
            </Popover>
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
