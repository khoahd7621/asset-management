import { Pagination } from 'antd';

import './ListUser.scss';

const itemRender = (_, type, originalElement) => {
  if (type === 'prev') {
    return <a> Previous</a>;
  }
  if (type === 'next') {
    return <a style={{ color: '#e5273d' }}> Next </a>;
  }
  return originalElement;
};

const Paging = ({ total, current, onChange }) => {
  return (
    <Pagination
      className="list-user-pagination"
      pageSize="3"
      onChange={onChange}
      current={current}
      total={total}
      itemRender={itemRender}
    />
  );
};

export default Paging;
