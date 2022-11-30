import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';

const AdminPrivateRoute = ({ children }) => {
  const user = useSelector((state) => state.user.user);

  if (user.role.toLocaleLowerCase() === 'admin') {
    return <>{children}</>;
  } else {
    return <Navigate to={'/'} />;
  }
};

export default AdminPrivateRoute;
