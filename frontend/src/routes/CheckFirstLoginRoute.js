import { useSelector } from 'react-redux';
import { Navigate } from 'react-router-dom';
import { adminRoute } from './routes';

const CheckFirstLoginRoute = ({ children }) => {
  const user = useSelector((state) => state.user.user);

  if (user.isFirstLogin) {
    return <Navigate to={`/${adminRoute.home}`} />;
  } else {
    return <>{children}</>;
  }
};

export default CheckFirstLoginRoute;
