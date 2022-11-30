import { useSelector } from 'react-redux';
import { Navigate, useLocation } from 'react-router-dom';
import { adminRoute, userRoute } from './routes';

const LoginProtectRoute = ({ children }) => {
  const location = useLocation();
  const user = useSelector((state) => state.user);

  const currentPath = location.pathname.slice(location.pathname.lastIndexOf('/') + 1, location.pathname.length);

  if (user.isAuthenticated === true && currentPath === '') {
    return <Navigate to={user.user.role === 'ADMIN' ? `/${adminRoute.home}` : `/${userRoute.home}`} />;
  }

  return <>{children}</>;
};

export default LoginProtectRoute;
