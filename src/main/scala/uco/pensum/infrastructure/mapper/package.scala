package uco.pensum.infrastructure

import uco.pensum.infrastructure.http.dtos.mapper.{
  MapperProductDTOInstances,
  MapperRecordsInstances
}

package object mapper {
  //object MapperProductEntity extends MapperProductEntityInstances //TODO: DAO entities to DTO
  object MapperProductDTO extends MapperProductDTOInstances
  object MapperRecords extends MapperRecordsInstances
}
