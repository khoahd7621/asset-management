import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { RightOutlined } from '@ant-design/icons';
import React from 'react';

const CustomBreadcrumb = ({ title, link }) => {
  return (
    <>
      <RightOutlined />
      <Link to={link}>
        <h3>{title}</h3>
      </Link>
    </>
  );
};

CustomBreadcrumb.propTypes = {
  title: PropTypes.string.isRequired,
};

export default CustomBreadcrumb;
