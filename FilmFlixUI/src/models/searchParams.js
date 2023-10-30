class SearchParams {
    // constructor(title, year,director,genre,limit,page,orderBy,direction) {
    //     this.title = title;
    //     this.year = year;
    //     this.director = director;
    //     this.genre = genre;
    //     this.limit = limit;
    //     this.page = page;
    //     this.orderBy = orderBy;
    //     this.direction = direction;
    //   }

      /*// Set an object property using a setter:
        person.title = "en"; */
      setTitle(title) {
        this.title = title;
      }


      setYear(year) {
        this.year = year;
      }

      setDirector(director) {
        this.director = director;
      }

      setGenre(genre) {
        this.genre = genre;
      }

      setLimit(limit) {
        this.limit = limit;
      }

      setPage(page) {
        this.page = page;
      }

      setOrderBy(orderBy) {
        this.orderBy = orderBy;
      }
      
      setDirection(direction) {
        this.direction = direction;
      }
      
}

export default SearchParams;