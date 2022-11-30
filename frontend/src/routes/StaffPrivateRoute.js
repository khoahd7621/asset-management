import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

const StaffPrivateRoute = ({ children }) => {
  const user = useSelector((state) => state.user.user);

  if (user.role.toLocaleLowerCase() === 'staff') {
    return <>{children}</>;
  } else {
    return <Navigate to={'/'} />;
  }
};

export default StaffPrivateRoute;
