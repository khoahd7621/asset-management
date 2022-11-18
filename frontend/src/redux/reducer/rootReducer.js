import { combineReducers } from 'redux';
import userReducer from '../slice/userSlice';

const rootReducer = combineReducers({
  user: userReducer,
});

export default rootReducer;
