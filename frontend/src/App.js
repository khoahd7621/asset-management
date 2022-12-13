import { useEffect } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';

import Admin from './layouts/Admin';
import Login from './layouts/Login';
import User from './layouts/User';
import {
  Home as AdminHome,
  CreateAsset,
  CreateAssignment,
  CreateUser,
  EditAsset,
  EditAssignment,
  EditUser,
  ManageAsset,
  ManageAssignment,
  ManageRequestReturn,
  ManageUser,
  Report,
} from './pages/admin';
import { Home as UserHome } from './pages/user';

import { adminRoute, userRoute } from './routes/routes';

import AdminPrivateRoute from './routes/AdminPrivateRoute';
import StaffPrivateRoute from './routes/StaffPrivateRoute';
import CheckFirstLoginRoute from './routes/CheckFirstLoginRoute';
import LoginProtectRoute from './routes/LoginProtectRoute';
import { fetchUserLoggedIn } from './redux/slice/userSlice';

const App = () => {
  const dispatch = useDispatch();
  const { isAuthenticated, isFirstLogin, user } = useSelector((state) => state.user);

  useEffect(() => {
    if (isAuthenticated && isFirstLogin) {
      dispatch(fetchUserLoggedIn(user.username));
    }
  }, []);

  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            <LoginProtectRoute>
              <Login />
            </LoginProtectRoute>
          }
        />
        <Route
          path={adminRoute.home}
          element={
            <AdminPrivateRoute>
              <Admin />
            </AdminPrivateRoute>
          }
        >
          <Route index element={<AdminHome />} />
          <Route
            path={adminRoute.manageUser}
            element={
              <CheckFirstLoginRoute>
                <ManageUser />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={`${adminRoute.manageUser}/${adminRoute.createUser}`}
            element={
              <CheckFirstLoginRoute>
                <CreateUser />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={adminRoute.manageAsset}
            element={
              <CheckFirstLoginRoute>
                <ManageAsset />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={`${adminRoute.manageAsset}/${adminRoute.createAsset}`}
            element={
              <CheckFirstLoginRoute>
                <CreateAsset />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={`${adminRoute.manageUser}/${adminRoute.editUser}/:id`}
            element={
              <CheckFirstLoginRoute>
                <EditUser />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={`${adminRoute.manageAsset}/${adminRoute.editAsset}/:id`}
            element={
              <CheckFirstLoginRoute>
                <EditAsset />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={adminRoute.manageAssignment}
            element={
              <CheckFirstLoginRoute>
                <ManageAssignment />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={`${adminRoute.manageAssignment}/${adminRoute.createAssignment}`}
            element={
              <CheckFirstLoginRoute>
                <CreateAssignment />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={`${adminRoute.manageAssignment}/${adminRoute.editAssignment}/:id`}
            element={
              <CheckFirstLoginRoute>
                <EditAssignment />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={adminRoute.requestForReturning}
            element={
              <CheckFirstLoginRoute>
                <ManageRequestReturn />
              </CheckFirstLoginRoute>
            }
          />
          <Route
            path={adminRoute.report}
            element={
              <CheckFirstLoginRoute>
                <Report />
              </CheckFirstLoginRoute>
            }
          />
        </Route>

        <Route
          path={userRoute.home}
          element={
            <StaffPrivateRoute>
              <User />
            </StaffPrivateRoute>
          }
        >
          <Route index element={<UserHome />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
