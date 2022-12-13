import { configureStore } from '@reduxjs/toolkit';

import { persistStore, persistReducer, REHYDRATE, PERSIST } from 'redux-persist';
import storage from 'redux-persist/lib/storage';

import { encryptTransform } from 'redux-persist-transform-encrypt';
import { createStateSyncMiddleware } from 'redux-state-sync';

import rootReducer from './reducer/rootReducer';

const persistConfig = {
  key: 'root',
  version: 1,
  storage,
  transforms: [
    encryptTransform({
      secretKey: 'SUPER_SUPER_SECRET_KEY',
      onError: function (error) {
        console.log('Error: ', error);
      },
    }),
  ],
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [REHYDRATE, PERSIST],
      },
    }).concat(
      createStateSyncMiddleware({
        blacklist: [REHYDRATE, PERSIST],
      }),
    ),
});
const persistor = persistStore(store);

export { store, persistor };
