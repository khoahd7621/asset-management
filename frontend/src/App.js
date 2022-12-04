import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Admin from './layouts/Admin';
import Login from './layouts/Login';
import User from './layouts/User';
import {
  CreateAssignment,
  CreateUser,
  EditAssignment,
  Home as AdminHome,
  ManageAsset,
  ManageUser,
} from './pages/admin';
import CreateAsset from './pages/admin/CreateAsset/CreateAsset';
import EditAsset from './pages/admin/EditAsset/EditAsset';
import { Home as UserHome } from './pages/user';
import EditUser from './pages/admin/EditUser/EditUser';
import AdminPrivateRoute from './routes/AdminPrivateRoute';
import { adminRoute, userRoute } from './routes/routes';
import StaffPrivateRoute from './routes/StaffPrivateRoute';
import CheckFirstLoginRoute from './routes/CheckFirstLoginRoute';
import ManageAssignment from './pages/admin/ManageAssignment/ManageAssignment';
import LoginProtectRoute from './routes/LoginProtectRoute';

const App = () => {
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
          {/* Add more route here - like format below ... */}
          {/* <Route path={adminRoute.[your-path]} element={[your-component]} /> */}
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
            path={`${adminRoute.manageUser}/${adminRoute.editUser}`}
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
