const convertDate = {
  convertStrDate: (dateStr) => {
    const date = new Date(dateStr);
    return (
      (date.getDate() > 9 ? date.getDate() : '0' + date.getDate()) +
      '/' +
      (date.getMonth() > 8 ? date.getMonth() + 1 : '0' + (date.getMonth() + 1)) +
      '/' +
      date.getFullYear()
    );
  },

  formatDate: (assignedDate) => {
    const initial = assignedDate?.split(/\//);
    const newdate = new Date([initial[1], initial[0], initial[2]].join('/'));
    return newdate?.getTime();
  },
};

export default convertDate;
