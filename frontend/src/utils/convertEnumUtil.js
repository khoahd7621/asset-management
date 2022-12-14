const convertEnum = {
  toGet: (str) => {
    const text = str?.replaceAll(' ', '_');
    return text?.toUpperCase();
  },

  toShow: (str) => {
    const text = str?.replaceAll('_', ' ');
    return text?.charAt(0).toUpperCase() + text?.slice(1).toLowerCase();
  },
};

export default convertEnum;
