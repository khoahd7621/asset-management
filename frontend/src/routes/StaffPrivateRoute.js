import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';
import { toast } from 'react-toastify';

const StaffPrivateRoute = ({ children }) => {
  const user = useSelector((state) => state.user.user);

  if (user.role.toLocaleLowerCase() === 'staff') {
    return <>{children}</>;
  } else {
    toast.error("You don't have permission to access this tab");
    return <Navigate to={'/'} />;
  }
};

export default StaffPrivateRoute;
