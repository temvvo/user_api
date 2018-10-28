package com.user.api_challenge.user.model;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role {
  @Id @GeneratedValue private long id;

  @Column(unique = true, nullable = false)
  private String name;

  public Role() {}

  public Role(String rolename) {
    this.name = rolename;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof Role)) {
      return false;
    } else {
      Role other = (Role) o;
      if (!other.canEqual(this)) {
        return false;
      } else if (this.getId() != other.getId()) {
        return false;
      } else {
        Object this$name = this.getName();
        Object other$name = other.getName();
        if (this$name == null) {
          if (other$name != null) {
            return false;
          }
        } else if (!this$name.equals(other$name)) {
          return false;
        }

        return true;
      }
    }
  }

  protected boolean canEqual(Object other) {
    return other instanceof Role;
  }

  public int hashCode() {
    int result = 1;
    long $id = this.getId();
    result = result * 59 + (int) ($id >>> 32 ^ $id);
    Object $name = this.getName();
    result = result * 59 + ($name == null ? 43 : $name.hashCode());
    return result;
  }

  public long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
