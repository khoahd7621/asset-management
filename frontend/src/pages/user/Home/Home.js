import { useEffect } from 'react';

import './Home.scss';

import TableMyAssignment from '../../../components/TableMyAssignment/TableMyAssignment';

const Home = () => {
  useEffect(() => {
    document.title = 'Home';
  }, []);

  return (
    <div className="staff-home-block">
      <div className="staff-home-block__title">My Assignment</div>
      <TableMyAssignment />
    </div>
  );
};

export default Home;
