import PropTypes from 'prop-types';
import { RightOutlined } from '@ant-design/icons';

const CustomBreadcrumb = ({ title }) => {
  return (
    <>
      <RightOutlined />
      <h3>{title}</h3>
    </>
  );
};

CustomBreadcrumb.propTypes = {
  title: PropTypes.string.isRequired,
};

export default CustomBreadcrumb;
