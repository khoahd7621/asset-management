import { Pagination } from 'antd';

import './Pagination.scss';

const CustomPagination = ({ onChange, current = 1, defaultPageSize = 20, total = 1, ...props }) => {
  return (
    <Pagination
      className="custom-pagination"
      showSizeChanger={false}
      onChange={onChange}
      current={current}
      defaultPageSize={defaultPageSize}
      total={total}
      itemRender={ItemRender}
      {...props}
    />
  );
};

const ItemRender = (_, type, originalElement) => {
  if (type === 'prev') {
    return <a>Previous</a>;
  }
  if (type === 'next') {
    return <a>Next</a>;
  }
  return originalElement;
};

export default CustomPagination;
