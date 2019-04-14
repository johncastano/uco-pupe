package uco.pensum.infrastructure

import uco.pensum.infrastructure.http.dtos.mapper.{
  MapperDTOInstances,
  MapperRecordsInstances,
  MapperResponseInstances
}

package object mapper {
  //object MapperProductEntity extends MapperProductEntityInstances //TODO: DAO entities to DTO
  object MapperRecords extends MapperRecordsInstances
  object MapperProductDTO extends MapperDTOInstances
  object MapperResponses extends MapperResponseInstances
}
