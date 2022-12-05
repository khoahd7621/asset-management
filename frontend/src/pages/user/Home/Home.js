import { useEffect } from 'react';
import TableMyAssignment from '../../../components/TableMyAssignment/TableMyAssignment';

import './Home.scss';

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
