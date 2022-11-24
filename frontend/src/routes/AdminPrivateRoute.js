import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';
import { toast } from 'react-toastify';

const AdminPrivateRoute = ({ children }) => {
  const user = useSelector((state) => state.user.user);

  if (user.role.toLocaleLowerCase() === 'admin') {
    return <>{children}</>;
  } else {
    toast.error("You don't have permission to access this tab");
    return <Navigate to={'/'} />;
  }
};

export default AdminPrivateRoute;
