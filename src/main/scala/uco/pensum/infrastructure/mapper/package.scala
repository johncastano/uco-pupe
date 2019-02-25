package uco.pensum.infrastructure

import uco.pensum.infrastructure.http.dtos.mapper.MapperRecordsInstances
import uco.pensum.infrastructure.http.dtos.mapper.MapperDTOInstances

package object mapper {
  //object MapperProductEntity extends MapperProductEntityInstances //TODO: DAO entities to DTO
  object MapperRecords extends MapperRecordsInstances
  object MapperProductDTO extends MapperDTOInstances
}
